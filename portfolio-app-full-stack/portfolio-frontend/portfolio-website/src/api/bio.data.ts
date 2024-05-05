import { useEffect, useState } from "react";
import fetchData from "./api.data";

interface UseBioDataFetchResult {
    bioData: About | null;
    loading: boolean;
    error: Error | null;
}

const useBioDataFetch = (): UseBioDataFetchResult => {
    const [bioData, setBioData] = useState<About | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<Error | null>(null);

    useEffect(() => {
        const fetchBioData = async () => {
            try {
                const responseData: ApiData = await fetchData();
                const { about } = responseData;

                if (about) {
                    setBioData(about);
                    setLoading(false);
                } else {
                    console.log('About data not found in response');
                }
            } catch (error) {
                console.error('Error fetching data:', error);
                if (error instanceof Error) {
                    setError(error);
                } else {
                    setError(new Error("An unkown error occurred"));
                }
                setLoading(false);
            }
        };
        fetchBioData();
    }, []);
    return { bioData, loading, error };
};

export default useBioDataFetch;