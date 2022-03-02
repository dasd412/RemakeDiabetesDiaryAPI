package com.dasd412.remake.api.service.security;

import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import static com.google.common.base.Preconditions.*;

@Service
public class FindInfoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WriterRepository writerRepository;

    public FindInfoService(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    public void getUserName(String email){
        logger.info("get user name");

    }

}
