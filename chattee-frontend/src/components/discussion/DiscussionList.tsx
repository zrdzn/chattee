import {DiscussionItem} from "./DiscussionItem";
import axios from "axios";
import {useEffect, useState} from "react";
import {transform} from "../../dateTransformer";

export const DiscussionList = () => {
    const [discussions, setDiscussions] = useState([]);

    useEffect(() => {
        axios.get('http://localhost:7070/api/v1/discussions', { withCredentials: true })
            .then((response) => {
                setDiscussions(response.data);
            })
            .catch((error) => {
                console.error(error)
            });
    }, []);

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
                        authorName={discussion.author.username}
                        createdDate={transform(discussion.createdAt)}
                        lastReplierName="zrdzn"
                        lastRepliedDate="January 23, 2023" />
                ))}
            </div>
        </div>
    );
}