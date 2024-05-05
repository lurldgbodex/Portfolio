import { useEffect, useState } from "react"
import fetchData from "./api.data"

interface LanguageFetchResult {
    languageData: language[] | null
    loading: boolean
    error: Error | null
}

const useLanguageDataFetch = () : LanguageFetchResult => {
    const [ languageState, setLanguageState ] = useState<LanguageFetchResult>({
        languageData: null,
        loading: true,
        error: null,
    });

    useEffect(() => {
        const fetchLanguage = async () => {
            try {
                const responseData: ApiData = await fetchData();
                const { languages } = responseData;

                if (languages) {
                    setLanguageState({ languageData: languages, loading: false, error: null })
                } else {
                    console.log("Language data not found in")
                }
            } catch (error) {
                if (error instanceof Error) {
                    setLanguageState({ languageData: null, loading: false, error})
                } else {
                    console.error(error)
                    setLanguageState({ languageData: null, loading: false, error: new Error("An error occurred")})
                }
            }
        };
        fetchLanguage();
    }, [])
    return languageState;
}

export default useLanguageDataFetch;