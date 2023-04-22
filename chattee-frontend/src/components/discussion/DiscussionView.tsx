import {DiscussionPost} from "./DiscussionPost";
import {useEffect, useState} from "react";
import axios from "axios";
import {DiscussionCreatePost} from "./DiscussionCreatePost";

export const DiscussionView = ({ id, authorName, title, description, createdDate }: any) => {
    const [posts, setPosts] = useState([]);

    useEffect(() => {
        axios.get(`http://localhost:7070/api/v1/discussions/${id}/posts`, { withCredentials: true })
            .then((response) => {
                setPosts(response.data)
            })
            .catch((error) => {
                console.error(error)
            });
    }, []);

    return (
        <>
            <div className="w-full p-4">
                <span className="flex items-center mt-5">
                    <p className="text-2xl">{title} - <span className="text-gray-500 text-xl">{description}</span></p>
                </span>
                <span className="flex items-center mb-5">
                    <p className="text-sm text-gray-400">Opened by {authorName} - {createdDate}</p>
                </span>
                {posts.map((post: any) => (
                    <DiscussionPost key={post.id}
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