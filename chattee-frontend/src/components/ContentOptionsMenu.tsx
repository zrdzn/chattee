import React from "react";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTrash, faEllipsis, faPenToSquare} from "@fortawesome/free-solid-svg-icons";
import {Axios} from "@/pages/_app";
import toast from "react-hot-toast";
import {useRouter} from "next/router";

export const ContentOptionsMenu: React.FC<{ id: number }> = (props: { id: number }) => {
    let router = useRouter();

    const handlePostDelete = async (event: any) => {
        event.preventDefault();

        Axios.delete(`posts/${props.id}`)
            .then(() => router.reload())
            .then(() => toast.success("You have successfully deleted post."))
            .catch(error => {
                console.error(error)
                toast.error(error.response.data.message)
            })
    };

    // TODO Add logic to Edit button

    return (
        <div className="dropdown dropdown-end">
            <label tabIndex={0} className="text-black">
                <FontAwesomeIcon className="mt-1 mr-1 hover:cursor-pointer" icon={faEllipsis} />
            </label>
            <ul tabIndex={0} className="dropdown-content menu p-4 shadow-md bg-gray-100 text-gray-800 rounded-lg w-52 space-y-4">
                <li>
                    <a>
                        <FontAwesomeIcon className="mt-1 mr-2 text-lg" icon={faPenToSquare} /> Edit
                    </a>
                </li>
                <li>
                    <a className="mt-2" onClick={handlePostDelete}>
                        <FontAwesomeIcon className="mt-1 mr-2 text-lg" icon={faTrash} /> Delete
                    </a>
                </li>
            </ul>
        </div>
    )
}
