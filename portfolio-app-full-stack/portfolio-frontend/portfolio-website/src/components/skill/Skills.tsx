import skillDataFetch from '../../api/skill..data';
import ProgressBar from '../helpers/ProgressBar';
import styles from './Skills.module.css'

const Skills = () => {

    const { skillData, loading, error } = skillDataFetch();

    if (loading) {
        return <p className={styles.noData}>Loading...</p>
    }

    if (error || !skillData) {
        return <p className={styles.noData}>Error fetching Skill data</p>
    }

  return (
    <div className={styles.skill}>
    <h2 className={styles.title}>Skills</h2>
    { skillData.map((skill, index) => (
        <div key={skill.id} className={styles.skillType} style={{ marginTop: index > 0 ? '20px': 0 }}>
            <h3 className={styles.subTitle}>{skill.name}</h3>
            { skill.skills.map((subSkill) => (
                <div key={subSkill.id} className={styles.skillTypeSkill}>
                    <div className={styles.score}>
                        <p className={styles.skillTit}>{subSkill.name}</p>
                    </div>
                    <ProgressBar score={100} />
                </div>
            ))}
        </div>
    ))}
</div>
  )
}

export default Skills