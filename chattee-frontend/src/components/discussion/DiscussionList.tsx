import {DiscussionItem} from "./DiscussionItem";
import {useEffect, useState} from "react";
import {transform} from "../../dateTransformer";
import toast from "react-hot-toast";
import {Axios} from "@/pages/_app";

export const DiscussionList = () => {
    const [discussions, setDiscussions] = useState([]);

    useEffect(() => {
        Axios.get('discussions')
            .then((response) => {
                setDiscussions(response.data);
            })
            .catch((error) => {
                if (error.response.status === 401) {
                    return
                }

                if (error.response.status === 403) {
                    toast.error("You are not authorized to view discussions.")
                    return
                }

                console.error(error)
                toast.error(error.response.data.message)
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
                        repliesAmount={discussion.postsAmount}
                        authorName={discussion.author.username}
                        createdDate={transform(discussion.createdAt)}
                        lastReplierName={discussion.latestPost ? discussion.latestPost.author.username : "No one"}
                        lastRepliedDate={discussion.latestPost ? transform(discussion.latestPost.createdAt) : "Never"} />
                ))}
            </div>
        </div>
    );
}