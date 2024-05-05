import useImageFetch from "../../api/image.data";

import { FaLongArrowAltRight } from "react-icons/fa";
import profileImage from "../../assets/seg-edited-removebg.png";

import styles from './Bio.module.css';
import useBioDataFetch from "../../api/bio.data";

const Bio: React.FC = () => {
  const { bioData, loading, error } = useBioDataFetch();
  const { imageUrl  } = useImageFetch(bioData?.user.imageUrl || '');

  if (loading) {
    return <p className={styles.loading}>Loading...</p>
  } 

  if (error || !bioData) {
    return <p className={styles.error}>Error fetching bio data...</p>
  }

  const {firstName, lastName } = bioData.user;
  const nameParts = [firstName, lastName].filter(Boolean);
  const fullName = nameParts.join(' ');


  return (
    <div className={styles.bio}>
        <div className={styles.bio_left}>
            <h1>
              I'm {fullName} <span className={styles.bio_span}>{bioData.title}</span>
            </h1>
            <p className={styles.summary}>{bioData.summary}</p>
            <button className={styles.button}>
              Contact Me <FaLongArrowAltRight />
            </button>
        </div>
        <div className={styles.bio_right}>
            <img src={imageUrl || profileImage} alt="Profile" className={styles.image} />
        </div>
    </div>
  )
}

export default Bio