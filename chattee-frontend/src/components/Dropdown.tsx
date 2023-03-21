import React, {useState} from 'react'
import {useRouter} from "next/router";
import axios from "axios";

export const Dropdown = ({ username }: any) => {
    let [errorMessage, setErrorMessage] = useState("")

    let router = useRouter();

    const handleSubmit = async (event: any) => {
        event.preventDefault();

        axios.post("http://localhost:7070/api/v1/auth/invalidate", {}, { withCredentials: true })
            .then(() => router.push("../"))
            .catch(error => {
                console.error(error)
                setErrorMessage(error.response.data.message)
            })
    };

    return (
        <div className="dropdown">
            <label tabIndex={0} className="btn m-1 text-white text-base">{username}</label>
            <ul tabIndex={0} className="dropdown-content menu p-4 shadow bg-gray-700 rounded-lg w-52">
                <li>
                    <a onClick={handleSubmit}>Logout</a>
                </li>
                {errorMessage &&
                    <li>
                        <a className="text-red-500">{errorMessage}</a>
                    </li>}
            </ul>
        </div>
    )
}
