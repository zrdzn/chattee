import React, {useRef, useState} from "react";

import {useAutosizeTextArea} from "../useAutosizeTextArea";

export const TextArea: React.FC<{ styles: string, placeholder: string, name: string, updatePostDetails: (content: string) => void }> = (props: {
    styles: string,
    placeholder: string,
    name: string,
    updatePostDetails: (content: string) => void
}) => {
    const [value, setValue] = useState("");
    const textAreaReference = useRef<HTMLTextAreaElement>(null);

    useAutosizeTextArea(textAreaReference.current, value);

    const handleChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
        setValue(event.target?.value);
        props.updatePostDetails(event.target?.value)
    };

    return (
        <textarea id={props.name}
                  name={props.name}
                  onChange={handleChange}
                  placeholder={props.placeholder}
                  ref={textAreaReference}
                  style={{resize: "none"}}
                  className={props.styles}
                  rows={3}
                  value={value} />
    );
}
