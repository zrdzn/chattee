import {TextArea} from "../TextArea";
import React, {useEffect, useState} from "react";
import axios from "axios";
import toast from "react-hot-toast";
import {useRouter} from "next/router";

export const DiscussionCreatePost: React.FC<{ discussionId: number }> = (props: { discussionId: number }) => {
    const [postDetails, setPostDetails] = useState<{
        content: string,
        discussionId: number
    }>({
        content: "", discussionId: 0
    })

    useEffect(() => setPostDetails(() => ({...postDetails, discussionId: props.discussionId})), [props])

    let router = useRouter();

    function updatePostContent(content: string) {
        setPostDetails((previous) => ({...previous, content: content }))
    }

    const handleSubmit = async (event: any) => {
        event.preventDefault();

        axios.post("http://localhost:7070/api/v1/posts", postDetails, { withCredentials: true })
            .then(() => {
                router.reload()
                toast.success("You have created new post!")
            })
            .catch(error => {
                if (error.response.status === 401) {
                    toast.error("You are not authenticated to do that.")
                    return
                }

                console.error(error)
                toast.error(error.response.data.message)
            })
    };

    return (
        <>
            <div className="c-card block bg-white shadow-md hover:shadow-xl rounded-lg overflow-hidden mb-4 mt-10">
                <div className="p-3">
                    <div className="flex">
                        <span className="text-lg text-gray-800">Create new post</span>
                    </div>
                </div>
                <div className="p-4">
                    <TextArea styles="p-3 bg-gray-50 hover:outline-none w-full focus:outline-none border hover:border-gray-200"
                              contentCallback={updatePostContent}
                              name="content"
                              placeholder="What would you like to post?" />
                </div>
                <div className="p-3">
                    <button className="bg-gray-800 hover:bg-blue-700 text-white font-bold py-4 px-8 rounded-full"
                            onClick={handleSubmit}>
                        Add post
                    </button>
                </div>
            </div>
        </>
    );
}
