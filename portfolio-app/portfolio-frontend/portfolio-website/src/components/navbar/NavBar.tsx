import { IoMdHome } from "react-icons/io";
import { FaUserGraduate, FaPiedPiperHat } from "react-icons/fa6";
import { FaBriefcase, FaFileCode } from "react-icons/fa";
import { BsChatLeftFill } from "react-icons/bs";

import styles from './NavBar.module.css'

const NavBar = () => {
  return (
    <div className={styles.nav}>
      <a href="#">
        <div className={styles.icon_div}>
        <IoMdHome  className={styles.icons}/>
        </div>
      </a>
      <a href="#">
        <div className={styles.icon_div}>
          <FaFileCode className={styles.icons} />
        </div>
      </a>
      <a href="#">
        <div className={styles.icon_div}>
          <FaUserGraduate className={styles.icons} />
        </div>
      </a>
      <a href="#">
        <div className={styles.icon_div}>
          <FaBriefcase className={styles.icons} />
        </div>
      </a>
      <a href="#">
        <div className={styles.icon_div}>
         <FaPiedPiperHat className={styles.icons} />
        </div>
      </a>
      <a href="#">
        <div className={styles.icon_div}>
          <BsChatLeftFill className={styles.icons} />
        </div>
      </a>
    </div>
  )
}

export default NavBar