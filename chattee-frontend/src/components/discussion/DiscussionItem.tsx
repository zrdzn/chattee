import {DiscussionItemStatistic} from "./DiscussionItemStatistic";

export const DiscussionItem = ({ id, title, description, repliesAmount, authorName, createdDate, lastReplierName, lastRepliedDate }: any) => {
    return (
        <>
            <div className="w-full sm:w-1/2 md:w-1/2 xl:w-1/4 p-4">
                <a href={"/discussions/" + id} className="c-card block bg-white shadow-md hover:shadow-xl rounded-lg overflow-hidden">
                    <div className="p-4">
                        <h2 className="mt-2 mb-2  font-bold">{title}</h2>
                        <p className="text-sm">{description}</p>
                        <div className="mt-3 flex items-center">
                            <span className="text-sm text-gray-400">Opened by {authorName}</span>
                        </div>
                    </div>
                    <div className="p-4 border-t border-b text-xs text-gray-700">
                        <DiscussionItemStatistic statistic="Replies" statisticValue={repliesAmount} />
                        <DiscussionItemStatistic statistic="Opened" statisticValue={createdDate} />
                        <DiscussionItemStatistic statistic="Last replied" statisticValue={lastRepliedDate} />
                        <DiscussionItemStatistic statistic="Last replied by" statisticValue={lastReplierName} />
                    </div>
                </a>
            </div>
        </>
    );
}