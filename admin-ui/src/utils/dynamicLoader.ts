import JSZip from "jszip";
import {base64ToBlob} from "@/utils/base64";

export const loadRemoteComponent = (scope: string, module: string) => {
    return new Promise(async (resolve, reject) => {
        try {
            // Initialize the sharing scope (shared modules like react, etc.)
            //@ts-ignore
            await __webpack_init_sharing__('default');
            //@ts-ignore
            const container = window[scope]; // Get the container loaded on the window object
            if (!container) {
                reject(new Error(`Remote scope ${scope} not found on window.`));
                return;
            }

            //@ts-ignore
            await container.init(__webpack_share_scopes__.default); // Initialize the container
            const factory = await container.get(module); // Get the module factory
            resolve(factory()); // Get the actual module
        } catch (e) {
            reject(e);
        }
    });
};

export const loadRemoteScript = (url: string): Promise<void> => {
    return new Promise((resolve, reject) => {
        try {
            const script = document.createElement('script');
            script.src = url;
            script.onload = (e) => {
                resolve();
            };
            script.onerror = reject;
            document.head.appendChild(script);
        } catch (e) {
            reject(e);
        }
    });
};


export const loadFileScript = (content: string): Promise<void> => {
    return new Promise((resolve, reject) => {
        try {
            try {
                eval(content);
            } catch (e) {
                resolve();
                return;
            }
            const encoder = new TextEncoder();
            const encodedContent = encoder.encode(content);
            const blob = new Blob([encodedContent], {type: 'application/javascript'});
            const url = URL.createObjectURL(blob);
            const script = document.createElement('script');
            script.src = url;
            script.onload = () => {
                resolve();
            };
            script.onerror = (e) => {
                reject(e);
            };
            document.head.appendChild(script);
        } catch (e) {
            reject(e);
        }
    });
};


export const loadZipJsFileScript = async (base64: string): Promise<void> => {
    return new Promise((resolve, reject) => {
        const file = base64ToBlob(base64, 'application/zip');
        if (file) {
            const zip = new JSZip();
            const content = file.arrayBuffer();
            zip.loadAsync(content).then((unzipped) => {
                const jsFiles: { relativePath: string, content: string }[] = [];

                const filePromises: Promise<void>[] = [];
                unzipped.forEach((relativePath, file) => {
                    if (relativePath.endsWith(".js")) {
                        const filePromise = file.async('text').then((text) => {
                            jsFiles.push({relativePath, content: text});
                        });
                        filePromises.push(filePromise);
                    }
                });

                Promise.all(filePromises).then(() => {
                    jsFiles.reduce((prevPromise: any, jsFile) => {
                        return prevPromise.then(() => {
                            return loadFileScript(jsFile.content).then(() => {
                                console.log('Load success file:', jsFile.relativePath);
                            });
                        });
                    }, Promise.resolve()).then(() => {
                        resolve();
                    }).catch(reject);
                }).catch(reject);
            }).catch(reject);
        } else {
            reject(new Error('Failed to convert base64 to Blob.'));
        }
    });
};
