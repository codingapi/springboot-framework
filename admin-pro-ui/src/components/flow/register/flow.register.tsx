import {registerComponent} from "@/framework/ComponentBus";
import PostponedFormView from "@/components/flow/components/PostponedFormView";
import ResultFormView from "@/components/flow/components/ResultFormView";
import {PostponedFormViewKey, ResultFormViewKey, UserSelectViewKey} from "@/components/flow/flow/types";
import UserSelectView from "@/components/flow/components/UserSelectView";

registerComponent(PostponedFormViewKey, PostponedFormView);
registerComponent(ResultFormViewKey, ResultFormView);
registerComponent(UserSelectViewKey, UserSelectView);
