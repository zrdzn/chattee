import {ErrorPage} from "../components/ErrorPage";

export default function Custom404() {
    return (
        <ErrorPage statusCode="404"
                   message="Page does not exist" />
    )
}
