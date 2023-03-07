import {DiscussionItem} from "./DiscussionItem";
import axios from "axios";
import {useEffect, useState} from "react";
import {cookies} from "next/headers";

export const DiscussionList = () => {
    const [discussions, setDiscussions] = useState([]);
    const [error, setError] = useState("An error occurred while loading discussions.")


    useEffect(() => {
        axios.get('http://localhost:7070/api/v1/discussions')
            .then((response) => {
                setDiscussions(response.data);
            })
            .catch((error) => {
                if (error.response.status === 401) {
                    setError("You do not have access to view discussions.")
                    return;
                }
                setError(error)
            });
    }, []);

    if (error) {
        return <h1>{error}</h1>
    }

    return (
        <div className="container mx-auto mt-24">
            <div className="flex flex-wrap">
                {discussions.map((discussion: any) => (
                    <DiscussionItem
                        key={discussion.id}
                        id={discussion.id}
                        title={discussion.title}
                        description={discussion.description}
                        repliesAmount={616}
                        authorName="zrdzn"
                        createdDate="January 23, 2023"
                        lastReplierName="zrdzn"
                        lastRepliedDate="January 23, 2023"
                    />
                ))}
            </div>
        </div>
    );
}