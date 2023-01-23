export const DiscussionItemStatistic = ({ statistic, statisticValue }: any) => {
    return (
        <>
            <span className="flex items-center mb-1">
                <p className="text-gray-500">{statistic}</p>&nbsp;{statisticValue}
            </span>
        </>
    );
}