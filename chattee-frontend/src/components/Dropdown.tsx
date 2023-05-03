import React from 'react'
import {useRouter} from "next/router";
import axios from "axios";
import toast from "react-hot-toast";
import {faRightFromBracket} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

export const Dropdown = ({ username }: any) => {
    let router = useRouter();

    const handleSubmit = async (event: any) => {
        event.preventDefault();

        axios.post("http://localhost:7070/api/v1/auth/invalidate", {}, { withCredentials: true })
            .then(() => router.push("../")
                .then(() => toast.success("You have successfully logged out."))
                .catch(error => {
                    console.error(error)
                    toast.error("Something went wrong while redirecting.")
                }))
            .catch(error => {
                console.error(error)
                toast.error("Could not log out.")
            })
    };

    return (
        <div className="dropdown">
            <label tabIndex={0} className="btn m-1 text-white text-base">{username}</label>
            <ul tabIndex={0} className="dropdown-content menu p-4 shadow bg-gray-700 rounded-lg w-52">
                <li>
                    <a onClick={handleSubmit}>
                        <FontAwesomeIcon className="mt-1 mr-1" icon={faRightFromBracket} /> Logout
                    </a>
                </li>
            </ul>
        </div>
    )
}
