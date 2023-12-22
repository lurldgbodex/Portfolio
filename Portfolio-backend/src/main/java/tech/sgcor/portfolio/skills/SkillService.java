package tech.sgcor.portfolio.skills;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;
import tech.sgcor.portfolio.shared.SharedService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Validated
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillTypeRepository skillTypeRepository;

    @Transactional
    public SkillType createSkill(@Valid SkillRequest request) {
        SkillType skillType = new SkillType();
        skillType.setName(request.getName());

        List<Skill> skills = request.getSkill().stream()
                .map(skillName -> {
                    Skill skill = new Skill();
                    skill.setName(skillName);
                    skill.setSkillType(skillType);
                    return skill;
                }).toList();

        skillType.setSkills(skills);
        return skillTypeRepository.save(skillType);
    }

    public GetSkill getSkills(long id) throws ResourceNotFound {
        var skill = skillTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Resource not found with id"));
        return new GetSkill(skill);
    }

    public CustomResponse updateSpecificSkill(
            long id, @Valid UpdateSkill request) throws ResourceNotFound {

        var skill = skillRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Resource not found with id"));

        skill.setName(request.getSkill());
        skillRepository.save(skill);

        return new CustomResponse(200, "Skill updated successfully", HttpStatus.OK);
    }

    @Transactional
    public CustomResponse updateSkill(
            SkillUpdateRequest request, long id) throws ResourceNotFound, BadRequestException {
        var updateSkillType = skillTypeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Resource not found with id"));

        boolean hasNonBlankField = Stream.of(
                Objects.toString(request.getName(), ""),
                Objects.toString(request.getSkill(), "")
        ).anyMatch(field -> !StringUtils.isBlank(field));

        if (!hasNonBlankField) {
            throw new BadRequestException("you need to provide a field to update");
        }

        updateSkillType.setName(SharedService.isNotBlank(request.getName()) ? request.getName() : updateSkillType.getName());
        skillTypeRepository.save(updateSkillType);

        List<Skill> skills = updateSkillType.getSkills();

        if (!skills.isEmpty()) {
            int noOfSkills = Math.min(skills.size(), request.getSkill().size());

            for (int i = 0; i < noOfSkills; i++) {
                Skill skill = skills.get(i);
                String newSkillName = request.getSkill().get(i);
                skill.setName(SharedService.isNotBlank(newSkillName) ? newSkillName : skill.getName());
            }
            skillRepository.saveAll(skills);
        }


        return new CustomResponse(200,
                "updated successfully", HttpStatus.OK);
    }

    @Transactional
    public CustomResponse deleteSkill(long id) throws ResourceNotFound {
        var skillType = skillTypeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Resource not found with id"));

        skillTypeRepository.deleteById(id);

        return new CustomResponse(200, "Deleted successfully", HttpStatus.OK);
    }
}
