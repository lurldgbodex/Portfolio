const fetchData = async (): Promise<ApiData> => {
    try {
        const response = await fetch('http://localhost:8080/api/users/1');
        if (!response.ok) {
            throw new Error('Failed to fetch data');
        }
        const data = await response.json();
        console.log("Data: ", data)

        return data;
    } catch (err) {
        console.error('Error fetching data:', err)
        throw err;
    }
};

export default fetchData;
