class GlobalEvent {
    private static instance: GlobalEvent;
    private events: Map<string, Function[]>;

    private constructor() {
        this.events = new Map();
    }

    public static getInstance(): GlobalEvent {
        if (!this.instance) {
            this.instance = new GlobalEvent();
        }
        return this.instance;
    }

    public on(eventName: string, callback: Function): void {
        const callbacks = this.events.get(eventName) || [];
        callbacks.push(callback);
        this.events.set(eventName, callbacks);
    }

    public emit(eventName: string, ...args: any[]): void {
        const callbacks = this.events.get(eventName);
        if (callbacks) {
            callbacks.forEach((callback) => {
                try {
                    callback(...args);
                } catch (err) {
                    console.error(
                        `Error executing callback for event: ${eventName}`,
                        err
                    );
                }
            });
        }
    }

    public off(eventName: string, callback?: Function): void {
        if (callback) {
            const callbacks = this.events.get(eventName);
            if (callbacks) {
                const callbackIndex = callbacks.indexOf(callback);
                if (callbackIndex > -1) {
                    callbacks.splice(callbackIndex, 1);
                    if (callbacks.length === 0) {
                        this.events.delete(eventName);
                    } else {
                        this.events.set(eventName, callbacks);
                    }
                }
            }
        } else {
            this.events.delete(eventName);
        }
    }
}

export const events = GlobalEvent.getInstance();
