import styles from './ProgressBar.module.css'

interface Props {
    score: number
}

const ProgressBar = ({ score }: Props) => {
    const fillWidth = `${score}%`;
    
    return (
        <div className={styles.progress_bar}>
            <div className={styles.progress_fill} style={{ width: fillWidth}}></div>
        </div>
    )
}

export default ProgressBar;