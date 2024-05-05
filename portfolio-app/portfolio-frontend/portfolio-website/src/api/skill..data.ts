import { useEffect, useState } from "react"
import fetchData from "./api.data";

interface SkillDataFetchResult {
    skillData: Skills[] | null,
    loading: boolean,
    error: Error | null
}

const skillDataFetch = () : SkillDataFetchResult => {
    const [skillState, setSkillState] = useState<SkillDataFetchResult>({
        skillData: null,
        loading: true,
        error: null
    });

    useEffect(() => {
        const fetchSkillData = async () => {
            try {
                const responseData: ApiData = await fetchData();
                const { skills } = responseData;

                if (skills) {
                    setSkillState({ skillData: skills, loading: false, error: null });
                }
            } catch (error) {
                if (error instanceof Error) {
                    console.error(error)
                    setSkillState({ skillData: null, loading: false, error})
                } else {
                    setSkillState({ skillData: null, loading: false, error: new Error("Error loading skill data")})
                }
            }
        };
        fetchSkillData();
    }, [])
    return skillState;
}

export default skillDataFetch;
