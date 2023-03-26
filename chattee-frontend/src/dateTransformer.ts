export const transform = (dateRaw: string) => {
    const date = new Date(Number(dateRaw) * 1000);
    return dateRaw ? date.toLocaleDateString() : `None`;
}
