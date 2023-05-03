import {ErrorPage} from "../components/ErrorPage";

export default function Custom500() {
    return (
        <ErrorPage statusCode="500"
                   message="Something went wrong with the server" />
    )
}