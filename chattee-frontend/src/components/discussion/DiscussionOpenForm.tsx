import {AuthFormHeader} from "../auth/AuthFormHeader";
import {TextArea} from "../TextArea";
import {useState} from "react";
import {useRouter} from "next/router";
import toast from "react-hot-toast";
import {Axios} from "@/pages/_app";

export const DiscussionOpenForm = () => {
    const [discussionDetails, setDiscussionDetails] = useState({ title: "", description: "" })
    const [postDetails, setPostDetails] = useState<{ content: string, discussionId: number }>({ content: "", discussionId: 0 })

    let router = useRouter();

    function updatePostContent(content: string) {
        setPostDetails((previous) => ({...previous, content: content }))
    }

    const handleSubmit = async (event: any) => {
        event.preventDefault();

        Axios.post("discussions", discussionDetails)
            .then(discussion => {
                setPostDetails((previous) => ({...previous, discussionId: discussion.data.id}))
                Axios.post("posts", postDetails)
                    .then(post => {
                        router.push(post.data.discussion.id.toString())
                            .then(() => toast.success("You have opened a new discussion!"))
                            .catch(error => {
                                console.error(error)
                                toast.error("Could not redirect.")
                            })
                    })
                    .catch(error => {
                        console.error(error)
                        toast.error(error.response.data.message)
                    })
            })
            .catch(error => {
                console.error(error)
                toast.error(error.response.data.message)
            })
    };

    const handleDiscussionChange = (event: any) => {
        setDiscussionDetails({ ...discussionDetails, [event.target.name]: event.target.value });
    };

    return (
        <>
            <div className="flex items-center justify-center h-screen">
                <div className="min-w-fit md:w-1/3 flex-col border bg-white px-10 py-14 shadow-md rounded-[4px] ">
                    <AuthFormHeader title="Open discussion"></AuthFormHeader>
                    <form method="post" onSubmit={handleSubmit}>
                        <div className="flex flex-col text-sm rounded-md">
                            <input className="mt-5 mb-5 rounded-[4px] border p-3 hover:outline-none focus:outline-none hover:border-gray-300"
                                   onChange={handleDiscussionChange}
                                   type="title"
                                   name="title"
                                   placeholder="TITLE" />
                            <input className="mb-5 border rounded-[4px] p-3 hover:outline-none focus:outline-none hover:border-gray-300"
                                   onChange={handleDiscussionChange}
                                   type="description"
                                   name="description"
                                   placeholder="DESCRIPTION" />
                            <TextArea styles="mb-5 border rounded-[4px] p-3 hover:outline-none focus:outline-none hover:border-gray-300"
                                      contentCallback={updatePostContent}
                                      name="content"
                                      placeholder="POST CONTENT" />
                        </div>
                        <button className="mt-5 w-full border p-2 bg-gray-800 text-white rounded-[4px] hover:bg-slate-700 scale-105 duration-300"
                                type="submit">Open a new discussion
                        </button>
                    </form>
                </div>
            </div>
        </>
    )
}