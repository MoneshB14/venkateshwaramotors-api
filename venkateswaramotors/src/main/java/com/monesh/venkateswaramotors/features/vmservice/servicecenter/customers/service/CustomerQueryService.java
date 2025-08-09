package com.monesh.venkateswaramotors.features.vmservice.servicecenter.customers.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.customers.dto.CustomerListItem;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.customers.dto.CustomerListResponse;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public CustomerListResponse listCustomers(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);

        List<AggregationOperation> ops = new ArrayList<>();

        // Optional search on registration or customer name
        if (search != null && !search.isBlank()) {
            ops.add(Aggregation.match(new Criteria().orOperator(
                    Criteria.where("vehicle_registration").regex(search, "i"),
                    Criteria.where("customer_name").regex(search, "i")
            )));
        }

        // Group by registration to compute totals and latest info
        ops.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "created_at")));
        ops.add(Aggregation.group("$vehicle_registration")
                .first("$customer_name").as("customerName")
                .first("$contact_number").as("contactNumber")
                .first("$created_at").as("lastVisit")
                .first("$service_type").as("lastServiceType")
                .first("$booking_id").as("lastBookingId")
                .count().as("totalVisits")
        );

        // Clone ops to compute total count before pagination
        List<AggregationOperation> countOps = new ArrayList<>(ops);
        countOps.add(Aggregation.count().as("total"));

        // Apply pagination
        ops.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "lastVisit")));
        ops.add(Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()));
        ops.add(Aggregation.limit(pageable.getPageSize()));

        Aggregation aggregation = Aggregation.newAggregation(ops);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "booked_services", Document.class);

        List<CustomerListItem> items = new ArrayList<>();
        for (Document d : results.getMappedResults()) {
            String reg = d.getString("_id");
            String name = d.getString("customerName");
            String contact = d.getString("contactNumber");
            Object lv = d.get("lastVisit");
            Instant lastVisit = null;
            if (lv instanceof java.util.Date) {
                lastVisit = ((java.util.Date) lv).toInstant();
            }
            Number totalVisitsNum = (Number) d.get("totalVisits");
            int totalVisits = totalVisitsNum == null ? 0 : totalVisitsNum.intValue();
            String lastServiceType = d.getString("lastServiceType");
            String lastBookingId = d.getString("lastBookingId");

            items.add(CustomerListItem.builder()
                    .vehicleRegistration(reg)
                    .customerName(name)
                    .contactNumber(contact)
                    .lastVisit(lastVisit)
                    .totalVisits(totalVisits)
                    .lastServiceType(lastServiceType)
                    .lastBookingId(lastBookingId)
                    .build());
        }

        // Total count
        Aggregation countAggregation = Aggregation.newAggregation(countOps);
        AggregationResults<Document> countResults = mongoTemplate.aggregate(countAggregation, "booked_services", Document.class);
        long total = 0;
        if (!countResults.getMappedResults().isEmpty()) {
            Number totalNum = (Number) countResults.getMappedResults().get(0).get("total");
            total = totalNum == null ? 0 : totalNum.longValue();
        }

        return CustomerListResponse.builder()
                .customers(items)
                .total(total)
                .page(page)
                .size(size)
                .build();
    }
}


