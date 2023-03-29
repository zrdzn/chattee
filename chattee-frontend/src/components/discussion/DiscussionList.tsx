import {DiscussionItem} from "./DiscussionItem";
import axios from "axios";
import {useEffect, useState} from "react";
import {transform} from "../../dateTransformer";
import toast from "react-hot-toast";

export const DiscussionList = () => {
    const [discussions, setDiscussions] = useState([]);

    useEffect(() => {
        axios.get('http://localhost:7070/api/v1/discussions', { withCredentials: true })
            .then((response) => {
                setDiscussions(response.data);
                if (discussions.length === 0) {
                    toast("There are no discussions yet.", {icon: '🙄'})
                }
            })
            .catch((error) => {
                console.error(error)
                toast.error("Could not load discussions.")
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
                        lastRepliedDate="January 23, 2023"
                    />
                ))}
            </div>
        </div>
    );
}