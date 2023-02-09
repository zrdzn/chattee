import { AuthFormHeader } from "./auth/AuthFormHeader";
import {useState} from "react";

export const LoginForm = () => {
    const [credentials, setCredentials] = useState({ email: "", password: "" })

    const handleSubmit = async (event: any) => {
        event.preventDefault();
        //await logIn(credentials);
    };

    const handleChange = (event: any) => {
        setCredentials({ ...credentials, [event.target.name]: event.target.value });
    };

    return (
        <>
            <div className="flex items-center justify-center h-screen">
                <div className="min-w-fit flex-col border bg-white px-10 py-14 shadow-md rounded-[4px] ">
                    <AuthFormHeader title="Sign in"></AuthFormHeader>
                    <form method="post">
                        <div className="flex flex-col text-sm rounded-md">
                            <input className="mb-5 rounded-[4px] border p-3 hover:outline-none focus:outline-none hover:border-gray-300"
                                   type="email"
                                   name="username"
                                   placeholder="EMAIL" />
                            <input className="border rounded-[4px] p-3 hover:outline-none focus:outline-none hover:border-gray-300"
                                   type="password"
                                   name="password"
                                   placeholder="PASSWORD" />
                        </div>
                        <button className="mt-5 w-full border p-2 bg-gray-800 text-white rounded-[4px] hover:bg-slate-700 scale-105 duration-300"
                                type="submit">Sign in
                        </button>
                    </form>
                    <div className="mt-5 flex justify-between text-sm text-gray-600">
                        <a href="#">Forgot password?</a>
                        <a href="../account/register">Sign up</a>
                    </div>
                    <div className="flex max-w-[15rem] justify-center mt-5 text-xs">
                        <p className="text-gray-400">By continuing, you agree to our Privacy Policy and User Agreement.</p>
                    </div>
                </div>
            </div>
        </>
    )
}