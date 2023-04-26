import {DiscussionView} from "../../components/discussion/DiscussionView";
import {useRouter} from "next/router";
import {useEffect, useState} from "react";
import axios from "axios";
import toast from "react-hot-toast";

export default function ViewDiscussion() {
    const [discussionDetails, setDiscussionDetails] = useState<{
        id: number,
        createdAt: string,
        title: string,
        description: string,
        author: any
    }>({
        id: 0,
        createdAt: "",
        title: "",
        description: "",
        author: {}
    })

    const router = useRouter()

    useEffect(() => {
        if (!router.isReady) return

        const { id } = router.query

        axios.get(`http://localhost:7070/api/v1/discussions/${id}`, { withCredentials: true })
            .then((discussion) => {
                setDiscussionDetails(() => ({
                    id: discussion.data.id,
                    createdAt: discussion.data.createdAt,
                    title: discussion.data.title,
                    description: discussion.data.description,
                    author: discussion.data.author
                }))
            })
            .catch((error) => {
                console.error(error)
                toast.error("Could not view this discussion.")
            });
    }, [router.isReady]);

    return (
        <>
            <DiscussionView id={discussionDetails.id}
                            author={discussionDetails.author}
                            title={discussionDetails.title}
                            description={discussionDetails.description}
                            createdDate={discussionDetails.createdAt} />
        </>
    )
}
