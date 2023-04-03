import {DiscussionView} from "../../components/discussion/DiscussionView";
import {useRouter} from "next/router";

export default function ViewDiscussion() {
    const {query, isReady} = useRouter()
    if (!isReady) {
        return <></>
    }

    const { id } = query

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
