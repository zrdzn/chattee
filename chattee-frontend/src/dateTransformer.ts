export const transform = (dateRaw: string) => {
    const date = new Date(dateRaw);
    return dateRaw ? date.toLocaleDateString() : `None`;
}
