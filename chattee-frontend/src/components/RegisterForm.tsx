import { AuthFormHeader } from "./auth/AuthFormHeader";
import {useState} from "react";
import {useRouter} from "next/router";
import toast from "react-hot-toast";
import { Axios } from "@/pages/_app";

export const RegisterForm = () => {
    const [credentials, setCredentials] = useState({
        email: "",
        password: "",
        username: ""
    })

    let router = useRouter();

    const handleSubmit = async (event: any) => {
        event.preventDefault();

        Axios.post("accounts/register", credentials)
            .then(() => router.push("../")
                .then(() => toast.success("You have been successfully registered."))
                .catch(error => {
                    console.error(error)
                    toast.error("Something went wrong while redirecting.")
                }))
            .catch(error => {
                console.error(error)
                toast.error(error.response.data.message)
            })
    };

    const handleChange = (event: any) => {
        setCredentials({ ...credentials, [event.target.name]: event.target.value });
    };

    return (
        <>
            <div className="flex items-center justify-center h-screen">
                <div className="min-w-fit flex-col border bg-white px-10 py-14 shadow-md rounded-[4px] ">
                    <AuthFormHeader title="Sign up"></AuthFormHeader>
                    <form method="post" onChange={handleChange} onSubmit={handleSubmit}>
                        <div className="flex flex-col text-sm rounded-md">
                            <input className="mb-5 rounded-[4px] border p-3 hover:outline-none focus:outline-none hover:border-gray-300"
                                   type="email"
                                   name="email"
                                   placeholder="EMAIL" />
                            <input className="mb-5 rounded-[4px] border p-3 hover:outline-none focus:outline-none hover:border-gray-300"
                                   type="username"
                                   name="username"
                                   placeholder="USERNAME" />
                            <input className="mb-5 border rounded-[4px] p-3 hover:outline-none focus:outline-none hover:border-gray-300"
                                   type="password"
                                   name="password"
                                   placeholder="PASSWORD" />
                            <input className="border rounded-[4px] p-3 hover:outline-none focus:outline-none hover:border-gray-300"
                                   type="password"
                                   name="retypePassword"
                                   placeholder="RETYPE PASSWORD"/>
                        </div>
                        <button
                            className="mt-5 w-full border p-2 bg-gray-800 text-white rounded-[4px] hover:bg-slate-700 scale-105 duration-300"
                            type="submit">Sign up
                        </button>
                    </form>
                    <div className="mt-5 flex justify-between text-sm text-gray-600">
                        <a href="../account/login">Already have an account?</a>
                    </div>
                </div>
            </div>
        </>
    )
}