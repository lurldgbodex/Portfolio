import useLanguageDataFetch from "../../api/language.data";
import ProgressBar from "../helpers/ProgressBar";
import styles from './language.module.css'

const Language = () => {

    const { languageData, loading, error } = useLanguageDataFetch();

    if (loading) {
        return <p className={styles.noData}>Loading...</p>
    }

    if (error || !languageData) {
        return <p className={styles.noData}>Error fetching Language data...</p>
    }

    const capitalizeFirstletter = (language: String) => {
        return language.charAt(0).toUpperCase() + language.slice(1)
    }

  return (
    <div className={styles.language}>
        <h2 className={styles.title}>Languages</h2>
        { languageData.map((language) => (
             <div key={language.id} className={styles.lang}>
             <div className={styles.score}>
                 <p className={styles.lang_tit}>{language.lang}</p>
                 <p className={styles.lang_sub}>{capitalizeFirstletter(language.level)}</p>
             </div>
             <ProgressBar score={100} />
        </div>
        ))}
    </div>
  )
}

export default Language