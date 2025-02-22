import {registerComponent} from "@/framework/ComponentBus";
import {PostponedFormViewKey, UserSelectFormViewKey} from "@/components/flow/types";
import PostponedFormView from "@/components/flow/plugins/PostponedFormView";
import UserSelectFormView from "@/components/flow/plugins/UserSelectFormView";

registerComponent(PostponedFormViewKey, PostponedFormView);
registerComponent(UserSelectFormViewKey, UserSelectFormView);

