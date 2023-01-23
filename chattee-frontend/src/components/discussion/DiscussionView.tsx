export const DiscussionView = ({ id, authorName, title, description, createdDate }: any) => {
    return (
        <>
            <div className="w-full p-4">
                <span className="flex items-center mt-5">
                    <p className="text-2xl">{title}</p>
                </span>
                <span className="flex items-center mb-5">
                    <p className="text-sm text-gray-400">Opened by {authorName}.</p>
                </span>
                <a href={"/discussions/" + id} className="c-card block bg-white shadow-md hover:shadow-xl rounded-lg overflow-hidden">
                    <div className="p-3">
                        <div className="flex items-center">
                            <span className="text-xs text-gray-400">{createdDate}</span>
                        </div>
                    </div>
                    <div className="p-4 border-t border-b">
                        {description}
                    </div>
                    <div className="p-4 border-b text-xs">
                        Custom user's signature
                    </div>
                </a>
            </div>
        </>
    );
}