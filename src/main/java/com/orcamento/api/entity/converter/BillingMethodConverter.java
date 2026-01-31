package com.orcamento.api.entity.converter;

import com.orcamento.api.entity.enums.BillingMethod;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BillingMethodConverter implements AttributeConverter<BillingMethod, String> {
    @Override
    public String convertToDatabaseColumn(BillingMethod attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public BillingMethod convertToEntityAttribute(String dbData) {
        return dbData == null ? null : BillingMethod.valueOf(dbData);
    }
}