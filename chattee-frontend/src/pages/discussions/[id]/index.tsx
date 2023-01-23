import {DiscussionView} from "../../../components/discussion/DiscussionView";
import {useRouter} from "next/router";

export default function Index() {
    const router = useRouter()
    const { id } = router.query

    return (
        <>
            <DiscussionView id={id}
                            authorName="zrdzn"
                            title="Another epic discussion"
                            description="Wow what an epic discussion we have here."
                            createdDate="January 23, 2023" />
        </>
    )
}
