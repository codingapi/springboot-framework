export const users = async () => {
    const data = [];
    for(let i=0;i<500;i++){
        data.push({
            id:i,
            name:`张三${i}`
        });
    }
    return data
}
