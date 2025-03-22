import {registerComponent} from "@/framework/ComponentBus";
import PostponedFormView from "@/components/flow/components/PostponedFormView";
import {
    PostponedFormViewKey,
    UserSelectFormViewKey,
} from "@/components/flow/types";
import UserSelectView from "@/components/flow/components/UserSelectView";

registerComponent(PostponedFormViewKey, PostponedFormView);
registerComponent(UserSelectFormViewKey, UserSelectView);
