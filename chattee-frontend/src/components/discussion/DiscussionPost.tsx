import {transform} from "../../dateTransformer";

export const DiscussionPost = ({ content, createdDate, authorName, authorAvatarUrl }: any) => {
    return (
        <>
            <div className="c-card block bg-white shadow-md hover:shadow-xl rounded-lg overflow-hidden mb-4">
                <div className="p-3">
                    <div className="flex items-center">
                        <span className="text-xs text-gray-400">{transform(createdDate)}</span>
                    </div>
                </div>
                <div style={{whiteSpace: "pre-wrap"}} className="p-4 border-t border-b">
                    {content}
                </div>
                <div className="p-4 flex border-b text-sm space-x-1">
                    <img src={authorAvatarUrl ? authorAvatarUrl : process.env.defaultAvatarUrl} alt="..." className="shadow w-5 rounded-full border-none" />
                    <span>{authorName}</span>
                </div>
            </div>
        </>
    );
}