import { useEffect, useState } from "react";

export const useFetch = (endpoint: any) => {
    const [data, setData] = useState<any[]>([]);
    const [error, setError] = useState();
    const [loading, setLoading] = useState(false);

    endpoint = `http://localhost:7070/${endpoint}`;

    useEffect(() => {
        setLoading(true);
        fetch(endpoint)
            .then(response => response.json())
            .then(setData)
            .catch(setError)
            .finally(() => setLoading(false));
    }, [endpoint]);

    return { data, error, loading };
};
