import myImage from '../../assets/seg-edited.jpg'
import Language from '../language/Language';
import styles from './SideBar.module.css'

import { MdDownload } from "react-icons/md";
import { FaXTwitter, FaLinkedinIn, FaGithub, FaWhatsapp } from "react-icons/fa6";
import Skills from '../skill/Skills';
import useBioDataFetch from '../../api/bio.data';
import useImageFetch from '../../api/image.data';

const SideBar: React.FC = () => {
  
  const { bioData, loading, error } = useBioDataFetch();
  const { imageUrl } = useImageFetch(bioData?.user.imageUrl || '');

  const handleResumeDownload = () => {
    if (!bioData) {
      return;
    }

    const resumeUrl : string = bioData!.cv;

    window.open(resumeUrl, '_blank')
  }

  return (
    <div className={styles.sideBar}>
      {
        loading ? (
          <p className={styles.noData}>Loading...</p>
        ) : error || !bioData ? (
          <p className={styles.noData}>Error fetching data</p>
        ) : (
          <>
            <div className={styles.image_div}>
              <img src={imageUrl || myImage} alt='my profile image' className={styles.image} />
            </div>
            <div className= {styles.bio}>
              <p className={styles.title}>{`${bioData.user.firstName} ${bioData.user.lastName}`}</p>
              <p className={styles.sub_title}>{bioData.title}</p>
              <div className={styles.socials} >
                  { bioData.twitter && <a className={styles.socLink} href={bioData.twitter} target="_blank" rel="noopener noreferrer"><FaXTwitter className={styles.soc} /></a> }
                  <a className={styles.socLink} href={bioData.linkedin} target="_blank" rel="noopener noreferrer"><FaLinkedinIn className={styles.soc} /></a>
                  <a className={styles.socLink} href={bioData.github} target="_blank" rel="noopener noreferrer"><FaGithub className={styles.soc} /></a>
                  <a className={styles.socLink} href={bioData.phoneNumber} target="_blank" rel="noopener noreferrer"><FaWhatsapp className={styles.soc} /></a>
              </div>
            </div>
          </>
        )
      } 
        <div className={styles.divider}></div>
        <Language />
        <div className={styles.divider}></div>
        <Skills />
        <div className={styles.divider}></div>
        <button className={styles.button} onClick={handleResumeDownload}>
          DOWNLOAD CV <MdDownload />
        </button>
    </div>
  )
}

export default SideBar