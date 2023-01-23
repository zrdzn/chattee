import {DiscussionItem} from "./DiscussionItem";

export const DiscussionList = () => {
    return (
        <>
            <div className="container mx-auto mt-24">
                <div className="flex flex-wrap">
                    <DiscussionItem id={1}
                                    title="Another epic discussion"
                                    description="Wow what an epic discussion we have here."
                                    repliesAmount={616}
                                    authorName="zrdzn"
                                    createdDate="January 23, 2023"
                                    lastReplierName="zrdzn"
                                    lastRepliedDate="January 23, 2023" />
                    <DiscussionItem id={5}
                                    title="Very cool title"
                                    description="The weather today is really sunny."
                                    repliesAmount={5}
                                    authorName="zrdzn"
                                    createdDate="January 20, 2023"
                                    lastReplierName="zrdzn"
                                    lastRepliedDate="January 20, 2023" />
                </div>
            </div>
        </>
    );
}