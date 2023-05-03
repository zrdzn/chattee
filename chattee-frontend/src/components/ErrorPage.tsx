import React from "react";

export const ErrorPage = ({ statusCode, message }: any) => {
    return (
        <div className="flex items-center justify-center mt-24">
            <div className="min-w-fit flex-col px-10 py-14 space-y-5">
                <div className="text-center text-3xl font-semibold">{statusCode}</div>
                <div className="text-2xl">{message}</div>
            </div>
        </div>
    )
}