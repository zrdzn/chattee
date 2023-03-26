export const DiscussionPost = ({ description, createdDate, authorName, authorAvatarUrl }: any) => {
    return (
        <>
            <div className="c-card block bg-white shadow-md hover:shadow-xl rounded-lg overflow-hidden mb-4">
                <div className="p-3">
                    <div className="flex items-center">
                        <span className="text-xs text-gray-400">{createdDate}</span>
                    </div>
                </div>
                <div className="p-4 border-t border-b">
                    {description}
                </div>
                <div className="p-4 flex border-b text-sm space-x-1">
                    <img src={authorAvatarUrl} alt="..." className="shadow w-5 rounded-full border-none" />
                    <span>{authorName}</span>
                </div>
            </div>
        </>
    );
}