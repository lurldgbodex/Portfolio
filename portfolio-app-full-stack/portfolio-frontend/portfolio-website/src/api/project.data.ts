import { useEffect, useState } from "react";
import fetchData from "./api.data";

interface ProjectDataFectchResult {
    projectData : Project[] | null;
    loading: boolean;
    error: Error | null;
}

const fetchProjectData = () : ProjectDataFectchResult => {
    const [projectState, setProjectState] = useState<ProjectDataFectchResult>({
        projectData: null,
        loading: true,
        error: null,
    });

    useEffect(() => {
        const getProjectData = async () => {
            try {
                const data: ApiData = await fetchData();
                const { projects } = data;

                if (projects) {
                    setProjectState({ projectData: projects, loading: false, error: null })
                }
            } catch (error) {
                console.error("Error fetching Project data.")
                if (error instanceof Error) {
                    setProjectState({ projectData: null, loading: false, error })
                } else {
                    setProjectState({ projectData: null, loading: false, error: new Error("An unexpected error occurred")})
                }
            }
        }
        getProjectData();
    }, []);
    return projectState;
}

export default fetchProjectData;