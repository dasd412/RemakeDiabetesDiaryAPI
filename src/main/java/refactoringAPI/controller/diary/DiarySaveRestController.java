package refactoringAPI.controller.diary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import refactoringAPI.controller.ApiResult;
import refactoringAPI.controller.diary.writer.WriterJoinRequestDTO;
import refactoringAPI.controller.diary.writer.WriterJoinResponseDTO;

import refactoringAPI.service.SaveDiaryService;

@RestController
public class DiarySaveRestController {
    /*
    컨트롤러 계층에는 엔티티가 사용되선 안되며 DTO 로 감싸줘야 한다.
     */

    private final SaveDiaryService saveDiaryService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiarySaveRestController(SaveDiaryService saveDiaryService) {
        this.saveDiaryService = saveDiaryService;
    }

    //todo writer 는 스프링 시큐리티 적용 후 빼낼 예정
    @PostMapping("/api/diary/writer")
    public ApiResult<WriterJoinResponseDTO> joinWriter(@RequestBody WriterJoinRequestDTO dto) {
        logger.info("join writer : " + dto.toString());
        return ApiResult.OK(new WriterJoinResponseDTO(saveDiaryService.saveWriter(dto.getName(), dto.getEmail(), dto.getRole())));
    }


}
