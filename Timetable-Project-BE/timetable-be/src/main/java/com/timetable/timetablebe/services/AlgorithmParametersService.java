package com.timetable.timetablebe.services;

import com.timetable.timetablebe.dtos.AlgorithmParametersDto;
import org.springframework.stereotype.Service;
import org.timetable.util.AlgorithmConstants;

@Service
public class AlgorithmParametersService {

    public AlgorithmParametersDto getAlgorithmParameters() {
        AlgorithmParametersDto algorithmParametersDto = new AlgorithmParametersDto();
        algorithmParametersDto.setNumberOfDays(AlgorithmConstants.NUMBER_OF_DAYS);
        algorithmParametersDto.setStartTime(AlgorithmConstants.START_TIME);
        algorithmParametersDto.setEndTime(AlgorithmConstants.END_TIME);
        algorithmParametersDto.setGeneralDuration(AlgorithmConstants.GENERAL_DURATION);

        return algorithmParametersDto;
    }

    public AlgorithmParametersDto setAlgorithmParameters(AlgorithmParametersDto algorithmParametersDto) {
        AlgorithmConstants.NUMBER_OF_DAYS = algorithmParametersDto.getNumberOfDays();
        AlgorithmConstants.START_TIME = algorithmParametersDto.getStartTime();
        AlgorithmConstants.END_TIME = algorithmParametersDto.getEndTime();
        AlgorithmConstants.GENERAL_DURATION = algorithmParametersDto.getGeneralDuration();

        return algorithmParametersDto;
    }
}
