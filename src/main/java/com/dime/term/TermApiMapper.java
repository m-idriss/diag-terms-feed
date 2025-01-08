package com.dime.term;

import com.dime.model.TermRecord;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.CDI)
public interface TermApiMapper {

  TermApiMapper INSTANCE = Mappers.getMapper(TermApiMapper.class);

  @Mapping(target = "word", source = "word", qualifiedByName = "lowercase")
  TermRecord toRecord(TermApi termApi);

  @Named("lowercase")
  default String lowercase(String word) {
    return word.toLowerCase();
  }

}