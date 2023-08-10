import {DiscussionPost} from "./DiscussionPost";
import {useEffect, useState} from "react";
import {DiscussionCreatePost} from "./DiscussionCreatePost";
import toast from "react-hot-toast";
import {transform} from "../../dateTransformer";
import {Axios} from "@/pages/_app";

export const DiscussionView = ({ id, author, title, description, createdDate }: any) => {
    const [posts, setPosts] = useState([]);

    useEffect(() => {
        Axios.get(`discussions/${id}/posts`)
            .then((response) => {
                setPosts(response.data)
            })
            .catch((error) => {
                console.error(error)
                toast.error(error.response.data.message)
            });
    }, [id]);

    return (
        <>
            <div className="w-full p-4">
                <span className="flex items-center mt-5">
                    <p className="text-2xl">{title} - <span className="text-gray-500 text-xl">{description}</span></p>
                </span>
                <span className="flex items-center mb-5">
                    <p className="text-sm text-gray-400">Opened by {author.username} - {transform(createdDate)}</p>
                </span>
                {posts.map((post: any) => (
                    <DiscussionPost key={post.id}
                                    id={post.id}
                                    content={post.content}
                                    createdDate={post.createdAt}
                                    authorName={post.author.username}
                                    authorAvatarUrl={post.author.avatarUrl} />
                ))}
                <DiscussionCreatePost discussionId={id} />
            </div>
        </>
    );
}