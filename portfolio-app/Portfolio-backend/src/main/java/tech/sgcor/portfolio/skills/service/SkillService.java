package tech.sgcor.portfolio.skills.service;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tech.sgcor.portfolio.exceptions.BadRequestException;
import tech.sgcor.portfolio.exceptions.ResourceNotFound;
import tech.sgcor.portfolio.shared.CustomResponse;
import tech.sgcor.portfolio.shared.SharedService;
import tech.sgcor.portfolio.skills.dto.SkillRequest;
import tech.sgcor.portfolio.skills.dto.SkillUpdateRequest;
import tech.sgcor.portfolio.skills.dto.UpdateSkill;
import tech.sgcor.portfolio.skills.entity.Skill;
import tech.sgcor.portfolio.skills.entity.SkillType;
import tech.sgcor.portfolio.skills.repository.SkillRepository;
import tech.sgcor.portfolio.skills.repository.SkillTypeRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillTypeRepository skillTypeRepository;

    @Transactional
    public SkillType createSkill(SkillRequest request) {
        SkillType skillType = new SkillType();
        skillType.setName(request.getName());
        skillType.setUserId(request.getUser_id());

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

    public List<SkillType> getSkills(Long userId) {
        return skillTypeRepository.findByUserId(userId);
    }

    public CustomResponse updateSpecificSkill(
            long id, UpdateSkill request) {

        var skill = skillRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Resource not found with id"));

        skill.setName(request.getSkill());
        skillRepository.save(skill);

        return new CustomResponse(200, "Skill updated successfully", HttpStatus.OK);
    }

    @Transactional
    public CustomResponse updateSkill(
            SkillUpdateRequest request, long id) {
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

        if (request.getSkill() != null) {
            var skillRequest = request.getSkill();
            int noOfSkills = Math.min(skills.size(), skillRequest.size());

            for (int i = 0; i < noOfSkills; i++) {
                Skill skill = skills.get(i);
                String newSkillName = skillRequest.get(i);
                skill.setName(SharedService.isNotBlank(newSkillName) ? newSkillName : skill.getName());
            }

            if (skillRequest.size() > noOfSkills) {
                for (int i = noOfSkills; i < skillRequest.size(); i++) {
                    Skill newSkill = new Skill();
                    newSkill.setName(skillRequest.get(i));
                    newSkill.setSkillType(updateSkillType);
                    skills.add(newSkill);
                }
            }
            skillRepository.saveAll(skills);
        }


        return new CustomResponse(200,
                "updated successfully", HttpStatus.OK);
    }

    @Transactional
    public CustomResponse deleteSkill(long id) {
        skillTypeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Resource not found with id"));

        skillTypeRepository.deleteById(id);

        return new CustomResponse(200, "Deleted successfully", HttpStatus.OK);
    }
}
