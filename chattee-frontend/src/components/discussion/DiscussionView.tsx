import {DiscussionPost} from "./DiscussionPost";

export const DiscussionView = ({ id, authorName, title, description, createdDate }: any) => {
    return (
        <>
            <div className="w-full p-4">
                <span className="flex items-center mt-5">
                    <p className="text-2xl">{title}</p>
                </span>
                <span className="flex items-center mb-5">
                    <p className="text-sm text-gray-400">Opened by {authorName}.</p>
                </span>
                <DiscussionPost description={description}
                                createdDate="January 24, 2023"
                                authorName="zrdzn"
                                authorAvatarUrl="https://www.spigotmc.org/data/avatars/l/732/732179.jpg?1654355022" />
                <DiscussionPost description="Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam vestibulum, nunc eu rutrum ornare, neque lorem rhoncus lorem, nec tincidunt libero ligula in erat. Ut non sem elit. Donec vel lacinia magna. Maecenas condimentum, ex eu auctor bibendum, ex libero eleifend nulla, vel auctor risus arcu finibus augue. Aliquam ac odio porttitor nunc volutpat convallis ut ac enim. Vestibulum ultrices metus a lacus sollicitudin, eget tempus elit rutrum. Sed mollis lorem id hendrerit efficitur. Sed id neque vitae quam vulputate vestibulum euismod sit amet erat. Aliquam quis sem vitae eros posuere porttitor. Quisque libero lectus, tristique a lacinia quis, ullamcorper sed sapien. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Ut ullamcorper massa at sapien porta dapibus. Aliquam tempus velit nisl, ut finibus dolor fringilla at. Pellentesque scelerisque tincidunt lorem vel facilisis. Phasellus gravida elit vulputate iaculis lacinia. "
                                createdDate="January 24, 2023"
                                authorName="zrdzn"
                                authorAvatarUrl="https://www.spigotmc.org/data/avatars/l/732/732179.jpg?1654355022" />
                <DiscussionPost description="Sed condimentum venenatis odio et gravida. Sed id est facilisis, molestie orci eget, condimentum felis. Vivamus nec tristique ante. Suspendisse non porttitor diam. Morbi vel magna non magna commodo suscipit et non massa. Vivamus ac eros augue. Nullam ullamcorper at nunc ac lacinia. Nam mattis enim et nibh porta, eget volutpat est elementum. In hac habitasse platea dictumst. Aliquam volutpat fringilla sem, et luctus diam elementum ut. Fusce quis porta velit. Sed eget cursus lectus, quis rhoncus enim. "
                                createdDate="January 24, 2023"
                                authorName="zrdzn"
                                authorAvatarUrl="https://www.spigotmc.org/data/avatars/l/732/732179.jpg?1654355022" />
            </div>
        </>
    );
}