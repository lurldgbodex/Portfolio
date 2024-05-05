import { useEffect, useState } from 'react';
import fetchProjectData from '../../api/project.data';
import styles from './Project.module.css';

const Project = () => {
  const { projectData, loading, error } = fetchProjectData();
  const [projectImages, setProjectImages] = useState<Record<string, string | null>>({});

  useEffect(() => {
    const fetchProjectImages = async () => {
      if (!projectData) return;

      const imageUrls: Record<string, string | null> = {};
      for (const project of projectData) {
        try {
          const response = await fetch(project.imageLink);
          if (response.ok) {
            const blob = await response.blob();
            const imageUrl = URL.createObjectURL(blob);
            imageUrls[project.imageLink] = imageUrl;
          } else {
            console.error(`Failed to fetch image for project ${project.name}`);
            imageUrls[project.imageLink] = null;
          }
        } catch (error) {
          console.error(`Error fetching image for project ${project.name}:`, error);
          imageUrls[project.imageLink] = null;
        }
      }

      setProjectImages(imageUrls);
    };

    fetchProjectImages();
  }, [projectData]);

  const handleClick = (url: string) => {
    window.location.href = url;
  };

  if (loading) {
    return <p className={styles.noData}>Loading...</p>;
  }

  if (error || !projectData) {
    return <p className={styles.noData}>{error?.message || 'Error fetching project data.'}</p>;
  }

  return (
    <div className={styles.project_list}>
      {projectData.map((project) => (
        <div
          key={project.id}
          className={styles.project_container}
          onClick={() => handleClick(project.url)}
        >
          <img
            src={projectImages[project.imageLink] || ''}
            alt={`cover image of ${project.name}`}
            className={styles.project_image}
          />
          <p className={styles.project_name}>{project.name}</p>
          <p className={styles.project_desc}>{project.description}</p>
        </div>
      ))}
    </div>
  );
};

export default Project;
