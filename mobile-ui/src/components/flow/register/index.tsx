import ComponentBus from "@/framework/ComponentBus";
import {PostponedFormViewKey, UserSelectFormViewKey} from "@/components/flow/types";
import PostponedFormView from "@/components/flow/plugins/PostponedFormView";
import UserSelectFormView from "@/components/flow/plugins/UserSelectFormView";

ComponentBus.getInstance().registerComponent(PostponedFormViewKey, PostponedFormView);
ComponentBus.getInstance().registerComponent(UserSelectFormViewKey, UserSelectFormView);

